package googledocs.legis;

import core.BotInstance;
import core.Tribe;
import googledocs.Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LegisMain extends Database {
	
	// Legal Objects: noble,  location, tribe,  city,   fortress
	//          Type: User,   int int,  Tribe,  City,   Fortress
	//   String Rep.: "N:id", "xx|yy",  "T:id", "C:id", "F:id"
	// Result Datatypes: bool, numeric, group
	
	// DESCRIPTION         RANK TERM                        RETURNS
	// FIELD               [00] Object.field                Object
	// BRACKETS:           [00] (term)                      term
	// NOT:                [01] !bool                       bool
	// Division:           [02] numeric    / numeric        numeric
	// Multiplication:     [02] numeric    * numeric        numeric
	//                          numeric    * bool           numeric
	// Addition:           [03] numeric    + numeric        numeric
	//                          numeric    + bool           numeric
	//                          bool       + bool           numeric
	// Subtraction:        [03] numeric    - numeric        numeric
	//                          numeric    - bool           numeric
	// Concatenation:      [03] Object     + Object         {Object}
	//                          {Object}   + Object         {Object}
	//                          {Object}   + {Object}       {Object}
	// Less Then           [05] numeric    < numeric        bool
	// Greater Then        [05] numeric    > numeric        bool
	// Equals:             [06] Object     = Object         bool
	// AND:                [07] bool       & bool           bool
	// XOR:                [08] bool       # bool           bool
	// OR:                 [09] bool       | bool           bool
	// Part of:            [10] Object     @ {Object}       bool
	
	// tribe.cities        returns {city}
	// tribe.fortresses    returns {fortress}
	// tribe.nobles        returns {noble}
	// tribe.cities.area   returns {location}
	// city.nobles         returns {noble}
	// city.area           returns {location}
	// city.tribe
	// noble.location      returns location
	// noble.tribe
	// bill.tribe          returns tribe
	// {Object}.<Field>    returns {<Field.type>}|Field
	// Object.<Field>      returns {<Field.type>}|Field
	
	public List<Bill> activeBills;
	BotInstance botInstance;
	
	private static final String SHEET_NAME = "LEGISLATION";
	
	public LegisMain(BotInstance botInstance) {
		this.botInstance = botInstance;
		loadLegislationFromSpreadsheet();
	}
	
	public List<Bill> loadLegislationFromSpreadsheet() {
		Tribe returnTribe = null;
		String returnFormula = null;
		int i = 1;
		List<Bill> bills = new ArrayList<>();
		do {
			try {
				List<Object> out = getRow(SHEET_NAME, i, 1, 2);
				returnTribe = Tribe.getTribeByName(out.get(0).toString());
				returnFormula = out.get(1).toString();
				i++;
				bills.add(new Bill(returnTribe, returnFormula));
				throw new NullPointerException();
			} catch(IOException e) {
				e.printStackTrace();
			} catch(NullPointerException e) {
				try {
					writeField(SHEET_NAME, i, 4, "ERROR");
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			} catch(Bill.ExpressionNotRecognizedException e) {
				try {
					writeField(SHEET_NAME, i, 4, "ExpressionNotRecognizedException: " + e.getMessage());
				} catch(IOException ex) {
					ex.printStackTrace();
				}
			}
		} while(returnTribe != null && returnFormula != null);
		activeBills = bills;
		return bills;
	}
	
}
