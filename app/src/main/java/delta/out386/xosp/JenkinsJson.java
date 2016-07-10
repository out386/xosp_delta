package delta.out386.xosp;

import java.io.Serializable;
import java.util.Date;
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
public class JenkinsJson implements Serializable {
    List<builds> builds;
    boolean isMalformed;

    static class builds implements Serializable {
        artifacts [] artifacts;
        String id;
        long timestamp;
        int date, downloadProgress = -2;
        String stringDate;
        boolean isDownloaded = false;
        fingerprint [] fingerprint;
    }

    static class artifacts implements Serializable{
        String fileName;
        String relativePath;
        int date;
        boolean isDelta;
        String downloadUrl;
    }

    static class fingerprint implements Serializable{
        String hash;
    }
}