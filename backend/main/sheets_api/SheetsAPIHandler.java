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

import java.awt.image.AreaAveragingScaleFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


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
        int firstOfTableInt = getFirstOfTableInt("'" + sheetTitle + "'!A1:1", false);
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
        List<String> dates = DateUtils.getMonthDates(monthYear);
        List<List<Object>> values = new ArrayList<>();
        for (String date : dates) {
            values.add(Arrays.asList(date));
        }
        createBudgetRows(monthYear, values, 2);
    }

    public void createHeaderColumns(String sheetTitle, List<String> columns) {
        int lastColumn = columns.size() + 1;
        char lastColChar = ColumnUtils.getColumnForNumber(lastColumn);
        String range = "'" + sheetTitle + "'!B1:" + lastColChar + "1";
        List<List<Object>> values = new ArrayList<>();
        values.add(new ArrayList<>());
        for (String column : columns) {
            values.get(0).add(StringUtils.capitalizeSentence(column));
        }
        updateSpreadsheetValues(range, values);
        GridRange gridRange = makeGridRange(getSheetId(sheetTitle),
                0, lastColumn, 0, 1);
        niceFormatCells(gridRange, "CENTER", true);
    }

    public void createBudgetRows(String sheetTitle, List<List<Object>> values, int startRow) {
        int lastRow = startRow + values.size() + 1;
        int numCols = 1;
        for (List<Object> rows : values)
            if (rows.size() > numCols) numCols = rows.size();
        char lastColumn = ColumnUtils.getColumnForNumber(numCols);
        String range = "'" +  sheetTitle + "'!A" + startRow + ":" + lastColumn + "" + lastRow;
        updateSpreadsheetValues(range, values);
    }

    public void createEstimatedSavingsRow(String sheetTitle, List<Object> values) {
        List<List<Object>> newValues = new ArrayList<>();
        newValues.add(values);
        int firstOfTableInt = getFirstOfTableInt("'" + sheetTitle + "'!A1:A", true);
        createBudgetRows(sheetTitle, newValues, firstOfTableInt + 1);
        extendProjectedExpenses(sheetTitle);
    }

    public void createProjectedExpensesRows(String sheetTitle, List<String> expenses, Map<String, Integer> expenseRows) {
        List<List<Object>> newExpenses = new ArrayList<>();
        newExpenses.add(Arrays.asList("EXPENSES:"));
        for (String expense : expenses) {
            newExpenses.add(Arrays.asList(expense));
        }
        newExpenses.add(Arrays.asList("TOTAL EXPENSES:", "=SUM(INDIRECT(ADDRESS(5,COLUMN(),4) &\":\"& ADDRESS(ROW()-1,COLUMN(),4)))"));
        newExpenses.add(Arrays.asList(""));
        newExpenses.add(Arrays.asList("INC - EXPENSE:", "=INDIRECT(ADDRESS(2, COLUMN())) - INDIRECT(ADDRESS(ROW()-2, COLUMN()))"));
        newExpenses.add(Arrays.asList(""));
        newExpenses.add(Arrays.asList("INC - EXPENSE CUL:", "=SUM(INDIRECT(ADDRESS(ROW()-2, 2)):INDIRECT(ADDRESS(ROW()-2, COLUMN())))"));
        List<List<Object>> values = new ArrayList<>();
        for (List<Object> row : newExpenses) {
            String expense = (String) row.get(0);
            if (expenses.contains(expense)) {
                List<Object> newRow = new ArrayList<>(row);
                newRow.add(expenseRows.get(expense));
                values.add(newRow);
            } else {
                values.add(row);
            }
        }
        int firstOfTableInt = getFirstOfTableInt("'" + sheetTitle + "'!A1:A", true);
        createBudgetRows(sheetTitle, values, firstOfTableInt + 1);
        GridRange gridRange = makeGridRange(getSheetId(sheetTitle), 0, 1, 0, 0);
        niceFormatCells(gridRange, "LEFT", false);
    }

    private void extendProjectedExpenses(String sheetTitle) {
        String rowRange = "'" + sheetTitle + "'!B2:B";
        ValueRange rowValuesRange = selectRangeOfValues(rowRange);
        String colRange = "'" + sheetTitle + "'!B1:1";
        ValueRange colValuesRange = selectRangeOfValues(colRange);
        if (rowValuesRange != null && colValuesRange != null) {
            List<List<Object>> rowValues = rowValuesRange.getValues();
            List<List<Object>> colValues = colValuesRange.getValues();
            int extendNum = colValues.get(0).size() - 1;
            for (int i = 1; i <= extendNum; i++) {
                for (List<Object> row : rowValues) {
                    if (!row.isEmpty()) row.add(row.get(0));
                }
            }
            int endRowNum = rowValues.size() + 1;
            char endColChar = ColumnUtils.getColumnForNumber(rowValues.get(0).size() + 1);
            String range = "'" + sheetTitle + "'!B2:" + endColChar + "" + endRowNum;
            updateSpreadsheetValues(range, rowValues);
        }
    }

    // Bolds range if specified and applies specified alignment, auto resizes range
    private void niceFormatCells(GridRange gridRange, String textAlignment, boolean bold) {
        CellFormat cellFormat = new CellFormat()
                .setTextFormat(new TextFormat().setBold(bold))
                .setHorizontalAlignment(textAlignment);
        CellData cellData = new CellData().setUserEnteredFormat(cellFormat);
        String fields = bold ? "userEnteredFormat.textFormat.bold,"
                + "userEnteredFormat.horizontalAlignment"
                : "userEnteredFormat.horizontalAlignment";
        RepeatCellRequest repeatCellRequest = new RepeatCellRequest()
                .setCell(cellData)
                .setRange(gridRange)
                .setFields(fields);

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

    private int getFirstOfTableInt(String range, boolean columns) {
        ValueRange occupiedRange = selectRangeOfValues(range);
        int firstOfTableInt = 0;
        if (occupiedRange != null) {
            firstOfTableInt = columns
                    ? occupiedRange.getValues().size() + 1
                    : occupiedRange.getValues().get(0).size() + 1;
        }
        return firstOfTableInt;
    }

    private ValueRange selectRangeOfValues(String range) {
        try {
            return getServiceInstance()
                    .spreadsheets()
                    .values()
                    .get(spreadsheetId, range)
                    .setValueRenderOption("FORMULA")
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