/*
 * Copyright (C) 2015 edgar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Player;

/**
 *
 * @author edgar
 */
public class Settings {
	private String bd_type = null;
	//private String jdbc_driver = null;
	private String db_url = null;
	private String user = null;
	private String passw = null;
	
	public String getJdbcDriver() {
		return "com." + bd_type + ".jdbc.Driver";
	}
	
	public String getDBUrl() {
		return "jdbc:" + bd_type + "://" + db_url;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getPassw() {
		return passw;
	}
	
	@Override
	public String toString() {
		String res;
		
		res = "bd_type=" + bd_type + ';';
		res += "db_url=" + db_url + ';';
		res += "user=" + user + ';';
		res += "passw=" + passw + ';';
		
		return res;
	}
}
