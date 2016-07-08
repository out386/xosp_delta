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
}