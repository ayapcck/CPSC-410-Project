package sheets_api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import utilities.ColumnUtils;
import utilities.DateUtils;
import utilities.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SheetsAPIHandler {
    private static final String APPLICATION_NAME = "Google Sheets DSL";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static SheetsAPIHandler sheetsAPIHandlerInstance = null;
    private static Sheets serviceInstance = null;
    private static String spreadsheetId = null;//"1qdwy9d3JOT2_-Qi17hy0gxBkldYull8YoLjPX-37JRA";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "./credentials.json";

    private SheetsAPIHandler() {}

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.

        InputStream in = SheetsAPIHandler.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static SheetsAPIHandler getSheetsAPIHandlerInstance() {
        if (sheetsAPIHandlerInstance == null) {
            sheetsAPIHandlerInstance = new SheetsAPIHandler();
        }
        return sheetsAPIHandlerInstance;
    }

    private static Sheets getServiceInstance() {
        if (serviceInstance == null) {
            // Build a new authorized API client service.
            try {
                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                serviceInstance = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
            } catch (Exception e) {
                System.out.println("Error getting instance of sheets api: " + e);
            }
        }
        return serviceInstance;
    }

    public void createSpreadsheet(String title) {
        if (spreadsheetId == null) {
            try {
                Spreadsheet spreadsheet = new Spreadsheet()
                        .setProperties(new SpreadsheetProperties()
                                .setTitle(title));
                spreadsheet = getServiceInstance().spreadsheets().create(spreadsheet)
                        .setFields("spreadsheetId")
                        .execute();
                spreadsheetId = spreadsheet.getSpreadsheetId();
            } catch (IOException e) {
                System.out.println("There was an issue creating the spreadsheet: " + e);
                System.exit(1);
            }
        } else {
            System.out.println("We already have a spreadsheet we're working with");
        }
    }

    // TODO: right now this creates a new sheet and doesn't rename the original sheet created
    // TODO: We need to ensure that if we're creating two sheets we rename the first one and then create a new one
    public void createSheet(String name) {
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setAddSheet(new AddSheetRequest()
                        .setProperties(new SheetProperties().setTitle(name))));
        batchUpdateRequest(requests,
                "There was an issue creating the sheet");
    }

    public void createTrackingColumns(String sheetTitle, List<String> expenses) {
        ValueRange occupiedRange = selectRangeOfValues("'" + sheetTitle + "'!A1:1");
        int firstOfTableInt = 0;
        if (occupiedRange != null) {
            firstOfTableInt = occupiedRange.getValues().get(0).size() + 1;
        }
        char firstOfTableChar = ColumnUtils.getColumnForNumber(firstOfTableInt);
        List<List<Object>> values = new ArrayList<>();
        values.add(Arrays.asList("Totals:"));
        for (String expense : expenses) {
            char expenseCol = ColumnUtils
                    .getColumnForNumber(expenses.indexOf(expense) + 1);
            String value = "=SUM(" + expenseCol + ":" + expenseCol + ")";
            values.add(Arrays.asList(StringUtils.capitalizeSentence(expense),
                    value));
        }
        char endOfTableChar = firstOfTableChar++;
        int endOfTableRows = expenses.size() + 2;
        String range = "'" + sheetTitle + "'!" + firstOfTableChar + "2:"
                + endOfTableChar + endOfTableRows;
        updateSpreadsheetValues(range, values);
        GridRange gridRange = makeGridRange(getSheetId(sheetTitle),
                firstOfTableInt, firstOfTableInt+1,1,2);
        niceFormatCells(gridRange, "LEFT", true);
    }

    public void createMonthRows(String monthYear) {
        List<List<Object>> values = new ArrayList<>();
        List<String> dates = DateUtils.getMonthDates(monthYear);
        for (String date : dates) {
            values.add(Arrays.asList(date));
        }
        int lastRow = dates.size() + 1;
        String range = "'" +  monthYear + "'!A2:A" + lastRow;
        updateSpreadsheetValues(range, values);
    }

    public void createExpensesColumns(String sheetTitle, List<String> expenses) {
        int lastColumn = expenses.size() + 1;
        char lastColChar = ColumnUtils.getColumnForNumber(lastColumn);
        String range = "'" + sheetTitle + "'!B1:" + lastColChar + "1";
        List<List<Object>> values = new ArrayList<>();
        values.add(new ArrayList<>());
        for (String expense : expenses) {
            values.get(0).add(StringUtils.capitalizeSentence(expense));
        }
        updateSpreadsheetValues(range, values);
        GridRange gridRange = makeGridRange(getSheetId(sheetTitle),
                0, lastColumn, 0, 1);
        niceFormatCells(gridRange, "CENTER", true);
    }

    // Bolds range if specified and applies specified alignment, auto resizes range
    private void niceFormatCells(GridRange gridRange, String textAlignment, boolean bold) {
        CellFormat cellFormat = new CellFormat()
                .setTextFormat(new TextFormat().setBold(bold))
                .setHorizontalAlignment(textAlignment);
        CellData cellData = new CellData().setUserEnteredFormat(cellFormat);
        RepeatCellRequest repeatCellRequest = new RepeatCellRequest()
                .setCell(cellData)
                .setRange(gridRange)
                .setFields("userEnteredFormat.textFormat.bold," +
                        "userEnteredFormat.horizontalAlignment");

        AutoResizeDimensionsRequest autoResizeDimensionsRequest = new AutoResizeDimensionsRequest()
                .setDimensions(new DimensionRange()
                        .setDimension("COLUMNS")
                        .setSheetId(gridRange.getSheetId())
                        .setStartIndex(gridRange.getStartColumnIndex())
                        .setEndIndex(gridRange.getEndColumnIndex()));

        List<Request> requests = new ArrayList<>();
        requests.add(new Request().setRepeatCell(repeatCellRequest));
        requests.add(new Request().setAutoResizeDimensions(autoResizeDimensionsRequest));

        batchUpdateRequest(requests,
                "There was an issue updating the formatting");
    }

    private int getSheetId(String sheetName) {
        try {
            Spreadsheet spreadsheet = getServiceInstance()
                    .spreadsheets()
                    .get(spreadsheetId).execute();
            for (Sheet sheet : spreadsheet.getSheets()) {
                if (sheet.getProperties().getTitle().equals(sheetName)) {
                    return sheet.getProperties().getSheetId();
                }
            }
        } catch (IOException e) {
            System.out.println("There was an issue retrieving the spreadsheet: " + e);
        }
        return -1;
    }

    public void updateSpreadsheetValues(String range, List<List<Object>> values) {
        ValueRange body = new ValueRange()
                .setValues(values);
        try{
            getServiceInstance()
                    .spreadsheets()
                    .values()
                    .update(spreadsheetId, range, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
        } catch (IOException e) {
            System.out.println("There was an issue creating columns: " + e);
        }
    }

    private ValueRange selectRangeOfValues(String range) {
        try {
            return getServiceInstance()
                    .spreadsheets()
                    .values()
                    .get(spreadsheetId, range)
                    .execute();
        } catch (IOException e) {
            System.out.println("There was an issue retrieving the range of values: " + e);
        }
        return null;
    }

    private void batchUpdateRequest(List<Request> requests, String errMsg) {
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest()
                .setRequests(requests);
        try {
            getServiceInstance()
                    .spreadsheets()
                    .batchUpdate(spreadsheetId, requestBody)
                    .execute();
        } catch (IOException e) {
            System.out.println(errMsg + ": " + e);
        }
    }
    private GridRange makeGridRange(int sheetID, int startColumn, int endColumn,
                                    int startRow, int endRow) {
        return new GridRange().setSheetId(sheetID)
                .setStartRowIndex(startRow)
                .setEndRowIndex(endRow)
                .setStartColumnIndex(startColumn)
                .setEndColumnIndex(endColumn);
    }

}