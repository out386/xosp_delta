package delta.out386.xosp;
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

import java.io.Serializable;

public class DeltaData implements Serializable{
    long targetSize = 0;
    float version = 2.1f;
    String sourceMd5 = null;
    String sourceDecMd5 = null;
    String targetMd5 = null;
    String deltaMd5 = null;
    String source;
    String target;
    String delta;

    public DeltaData(String source, String target, String delta)
    {
        this.source = source;
        this.target = target;
        this.delta = delta;
    }
}
