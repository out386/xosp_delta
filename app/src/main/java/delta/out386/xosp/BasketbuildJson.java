package delta.out386.xosp;

import java.util.StringTokenizer;

/*
 * Copyright (C) 2016 Ritayan Chakraborty (out386)
 */
/*
 * This file is part of XOSPDelta.
 *
 * XOSPDelta is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * XOSPDelta is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with XOSPDelta. If not, see <http://www.gnu.org/licenses/>.
 */
public class BasketbuildJson {
    folder [] folders;
    file [] files;
}
class folder {
    String folder;
}
class file {
    String file;
    String filesize;
    String filemd5;
    long date;

    public void process() {
        String [] fileComponents = file.split("[" + Constants.ROM_ZIP_DELIMITER + "]");
        date = Integer.parseInt(fileComponents[Constants.ROM_ZIP_DATE_LOCATION - 1]);
    }
}
