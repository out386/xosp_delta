package delta.out386.xosp;

import android.util.Log;

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
public class JenkinsJson {
    builds [] builds;
    boolean isMalformed;

    static class builds {
        artifacts [] artifacts;
        String id;
        fingerprint [] fingerprint;
    }

    static class artifacts {
        String fileName;
        String relativePath;
        long date;
        boolean isDelta;
    }

    static class fingerprint {
        String hash;
    }
    public void process(JenkinsJson updates) {
        /* Here, each build (Jenkins "job") has just one artifact. That's just how the server's set up.
         * That is why the loop iterates over "builds" and not over both "builds" and "artifacts".
         * This behaviour may or may not be changed later.
         */
        for(int i = 0; i < updates.builds.length; i++) {
            if(updates.builds[i].artifacts.length == 0)
                continue;
            int dateIndex = 0;
            String[] fileComponents = updates.builds[i].artifacts[0].fileName.split("[" + Constants.ROM_ZIP_DELIMITER + "]");

            // As deltas will have all their indices offset one place to the right
            if(fileComponents[0].equals("delta")) {
                dateIndex = Constants.ROM_ZIP_DATE_LOCATION;
                updates.builds[i].artifacts[0].isDelta = true;
            }
            else
                dateIndex = Constants.ROM_ZIP_DATE_LOCATION -1;
            try {
                updates.builds[i].artifacts[0].date = Integer.parseInt(fileComponents[dateIndex]);
            }
            catch (NumberFormatException e) {
                Log.e(Constants.TAG, "JSON parsing : ", e);
                isMalformed = true;
            }
        }
    }
}