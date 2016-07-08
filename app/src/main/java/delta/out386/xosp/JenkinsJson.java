package delta.out386.xosp;

import java.util.Iterator;
import java.util.List;

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
    List<builds> builds;
    boolean isMalformed;

    static class builds {
        artifacts [] artifacts;
        String id;
        fingerprint [] fingerprint;
    }

    static class artifacts {
        String fileName;
        String relativePath;
        int date;
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
        Iterator<builds> buildIterator = updates.builds.iterator();
        while (buildIterator.hasNext()){
            builds currentBuild = buildIterator.next();
            if(currentBuild.artifacts.length == 0) {
                buildIterator.remove();
                continue;
            }
            Tools.RomDateType romType = new Tools().romZipDate(currentBuild.artifacts[0].fileName, false);
            if(romType.date == -1) {
                isMalformed = true;
                return;
            }
            currentBuild.artifacts[0].date = romType.date;
            currentBuild.artifacts[0].isDelta = romType.isDelta;
        }
    }
}