package googledocs;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.checkerframework.common.value.qual.IntRange;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Database {
	
	private static final String FILE_ID = "1PAGRLQj9QsdknTuMapii0BIr5au16AHDvTdbCPZnsnI";
	private final Sheets service;
	
	public Database() {
		service = GoogleSheets.getService();
	}
	
	public Object               getField(String sheet, int row, @IntRange(from = 1, to = 26) int column) throws IOException {
		ValueRange vr = service.spreadsheets().values().get(
				FILE_ID,  ((sheet != null && !sheet.equals("")) ? (sheet + "!") : "") + convertIndexToColumn(column) + row
		).execute();
		return vr.getValues() != null ? vr.getValues().get(0).get(0) : null;
	}
	public List<Object>         getRow(String sheet, int row, @IntRange(from = 1, to = 26) int column1, @IntRange(from = 1, to = 26) int column2) throws IOException {
		ValueRange vr = service.spreadsheets().values().get(
				FILE_ID, (sheet != null && !sheet.equals("")) ? (sheet + "!") : "" + convertIndexToColumn(column1) + (row + 1) +
						":" + convertIndexToColumn(column2) + (row + 1)
		).execute();
		return vr.getValues() != null ? vr.getValues().get(0) : null;
	}
	public List<Object>         getColumn(String sheet, @IntRange(from = 1, to = 26) int column, int row1, int row2) throws IOException {
		ValueRange vr = service.spreadsheets().values().get(
				FILE_ID,  (sheet != null && !sheet.equals("")) ? (sheet + "!") : "" + convertIndexToColumn(column) + (row1 + 1) +
						":" + convertIndexToColumn(column) + (row2 + 1)
		).execute();
		return vr.getValues().stream().flatMap(List::stream).collect(Collectors.toList());
	}
	public List<List<Object>>   getRange(String sheet, int row1, @IntRange(from = 1, to = 26) int column1, int row2, @IntRange(from = 1, to = 26) int column2) throws IOException {
		ValueRange vr = service.spreadsheets().values().get(
				FILE_ID, (sheet != null && !sheet.equals("")) ? (sheet + "!") : "" + convertIndexToColumn(column1) + (row1 + 1) +
						":" + convertIndexToColumn(column2) + (row2 + 1)
		).execute();
		return vr.getValues() != null ? vr.getValues() : null;
	}
	
	public UpdateValuesResponse writeField(String sheet, int row, @IntRange(from = 1, to = 26) int column, Object value) throws IOException {
		String range = sheet + '!' + convertIndexToColumn(column) + row;
		ValueRange vr = new ValueRange()
				.setValues(
						Collections.singletonList(Collections.singletonList(value))
				);
		return service.spreadsheets().values().update(FILE_ID, range, vr)
						.setValueInputOption("RAW")
						.execute();
	}
	public UpdateValuesResponse writeRow(String sheet, int row, @IntRange(from = 1, to = 26) int column1, @IntRange(from = 1, to = 26) int column2, List<Object> value) throws IOException {
		String range = sheet + '!' + convertIndexToColumn(column1) + row + ':' + convertIndexToColumn(column2) + row;
		ValueRange vr = new ValueRange()
				.setValues(
						Collections.singletonList(value)
				);
		return service.spreadsheets().values().update(FILE_ID, range, vr)
				.setValueInputOption("RAW")
				.execute();
	}
	public UpdateValuesResponse writeColumn(String sheet, int row1, int row2, @IntRange(from = 1, to = 26) int column, List<Object> values) throws IOException {
		String range = sheet + '!' + convertIndexToColumn(column) + row1 + ':' + convertIndexToColumn(column) + row2;
		ValueRange vr = new ValueRange()
				.setValues(values.stream().map(List::of).collect(Collectors.toList()));
		return service.spreadsheets().values().update(FILE_ID, range, vr)
				.setValueInputOption("RAW")
				.execute();
	}
	public UpdateValuesResponse writeRange(String sheet, int row1, @IntRange(from = 1, to = 26) int column1, int row2, @IntRange(from = 1, to = 26) int column2, List<List<Object>> value) throws IOException {
		String range = sheet + '!' + convertIndexToColumn(column1) + row1 + ':' + convertIndexToColumn(column2) + row2;
		ValueRange vr = new ValueRange()
				.setValues(
						value
				);
		return service.spreadsheets().values().update(FILE_ID, range, vr)
				.setValueInputOption("RAW")
				.execute();
	}
	
	private char convertIndexToColumn(int column) {
		return (char) Math.min(('A' + column - 1),'Z');
	}
	
}
