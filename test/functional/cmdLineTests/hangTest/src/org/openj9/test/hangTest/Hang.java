/*******************************************************************************
 * Copyright (c) 2021, 2021 IBM Corp. and others
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which accompanies this
 * distribution and is available at https://www.eclipse.org/legal/epl-2.0/
 * or the Apache License, Version 2.0 which accompanies this distribution and
 * is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * This Source Code may also be made available under the following
 * Secondary Licenses when the conditions for such availability set
 * forth in the Eclipse Public License, v. 2.0 are satisfied: GNU
 * General Public License, version 2 with the GNU Classpath
 * Exception [1] and GNU General Public License, version 2 with the
 * OpenJDK Assembly Exception [2].
 *
 * [1] https://www.gnu.org/software/classpath/license.html
 * [2] http://openjdk.java.net/legal/assembly-exception.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 OR LicenseRef-GPL-2.0 WITH Assembly-exception
 *******************************************************************************/
package org.openj9.test.hangTest;
import java.io.IOException;

public class Hang {
    public static void main(String [] args) {
        Hook hook = new Hook();
        System.out.println("adding hook ... ");
        Runtime.getRuntime().addShutdownHook(hook);
        hook.start();
        new Sleep().start();
        System.out.println("Exiting ... ");
    }

    private static class Hook extends Thread {
        public void run() {
			int i = 0;
            while(i < 200) {
                try {
                    System.out.println("Sleep inside Hook ...." + i);
                    Thread.sleep(1000);
					i++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static class Sleep extends Thread {
        public void run() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                long pid = ProcessHandle.current().pid();
                System.out.println(pid);
                // (javacore filename set on command line)
                String cmdLine = "kill -3 " + pid;
                System.out.println("Executing another process: " + cmdLine);
                Runtime rt = Runtime.getRuntime();
                @SuppressWarnings("unused")
                Process pr = rt.exec(cmdLine);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.err.print("Failed to send kill signal, IOException");
                System.exit(-2);
            }
        }
    }
}