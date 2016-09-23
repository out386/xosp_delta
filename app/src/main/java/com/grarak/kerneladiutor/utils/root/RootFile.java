/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
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
 * along with XOSPDelta.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.utils.root;

import com.grarak.kerneladiutor.utils.Utils;
import eu.chainfire.libsuperuser.Shell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by willi on 30.12.15.
 */
public class RootFile {

    private final String mFile;

    public RootFile(String file) {
        mFile = file;
    }

    public String getName() {
        return new File(mFile).getName();
    }

    public void mkdir() {
        Shell.SU.run("mkdir -p '" + mFile + "'");
    }

    public RootFile mv(String newPath) {
        Shell.SU.run("mv -f '" + mFile + "' '" + newPath + "'");
        return new RootFile(newPath);
    }

    public void cp(String path) {
        Shell.SU.run("cp -r '" + mFile + "' '" + path + "'");
    }

    public void write(String text, boolean append) {
        String[] array = text.split("\\r?\\n");
        if (!append) delete();
        for (String line : array) {
            Shell.SU.run("echo '" + line + "' >> " + mFile);
        }
        Shell.SU.run("chmod 755 " + mFile);
    }

    public String execute(String... arguments) {
        StringBuilder args = new StringBuilder();
        String result;
        for (String arg : arguments) {
            args.append(" \"").append(arg).append("\"");
        }
        try {
            result = Shell.SU.run(mFile + args.toString()).get(0);
        } catch(IndexOutOfBoundsException e) {
            return null;
        }
        return result;
    }

    public void delete() {
        Shell.SU.run("rm -r '" + mFile + "'");
    }

    public List<String> list() {
        List<String> list = new ArrayList<>();
        String files;
        try {
            files = Shell.SU.run("ls '" + mFile + "/'").get(0);
        } catch(IndexOutOfBoundsException e) {
            files = null;
        }
        if (files != null) {
            // Make sure the files exists
            for (String file : files.split("\\r?\\n")) {
                if (file != null && !file.isEmpty() && Utils.existFile(mFile + "/" + file)) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    public List<RootFile> listFiles() {
        List<RootFile> list = new ArrayList<>();
        String files = Shell.SU.run("ls '" + mFile + "/'").get(0);
        if (files != null) {
            // Make sure the files exists
            for (String file : files.split("\\r?\\n")) {
                if (file != null && !file.isEmpty() && Utils.existFile(mFile + "/" + file)) {
                    list.add(new RootFile(mFile + "/" + file));
                }
            }
        }
        return list;
    }

    public boolean isDirectory() {
        return "true".equals(Shell.SU.run("[ -d " + mFile + " ] && echo true"));
    }

    public RootFile getParentFile() {
        return new RootFile(new File(mFile).getParent());
    }

    public RootFile getRealPath() {
        return new RootFile(Shell.SU.run("realpath \"" + mFile + "\"").get(0));
    }

    public boolean isEmpty() {
        String o = null;
        try {
                o = Shell.SU.run("busybox find '" + mFile + "' -mindepth 1 | read || echo false").get(0);
                } catch(IndexOutOfBoundsException e) { return false; }
        return "false".equals(o);
    }

    public boolean exists() {
        String output = Shell.SU.run("[ -e " + mFile + " ] && echo true").get(0);
        return output != null && output.equals("true");
    }

    public String readFile() {
        return Shell.SU.run("cat '" + mFile + "'").get(0);
    }

    public String toString() {
        return mFile;
    }

}
