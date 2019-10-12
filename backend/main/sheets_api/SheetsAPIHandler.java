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
import org.mortbay.util.IO;
import utilities.ColumnUtils;
import utilities.DateUtils;

import javax.sound.midi.SysexMessage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static utilities.DateUtils.getMonthDates;


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

    public void createExpensesColumns(List<String> expenses) {
        int lastColumn = expenses.size() + 1;
        char lastColChar = ColumnUtils.getColumnForNumber(lastColumn);
        String range = "'October 2019'!B1:" + lastColChar + "1";
        List<List<Object>> values = new ArrayList<>();
        values.add(new ArrayList<>());
        for (String expense : expenses) {
            values.get(0).add(expense);
        }
        updateSpreadsheetValues(range, values);
        // TODO: create get Sheet id method
        GridRange gridRange = new GridRange()
                .setSheetId(getSheetId("October 2019"))
                .setStartColumnIndex(1)
                .setEndColumnIndex(lastColumn);
        formatHeaderRows(gridRange);
    }

    // Centers and bolds range (intended for header rows)
    public void formatHeaderRows(GridRange gridRange) {
        CellFormat cellFormat = new CellFormat()
                .setTextFormat(new TextFormat().setBold(true))
                .setHorizontalAlignment("CENTER");
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
                        .setStartIndex(1)
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

}