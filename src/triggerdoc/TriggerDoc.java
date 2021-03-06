/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.6 $
 * Last changed:   $Date: 2017/11/18 02:20:35CET $
 */

package triggerdoc;

import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import static java.lang.System.exit;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static triggerdoc.Copyright.programVersion;
import static triggerdoc.Copyright.usage;
import triggerdoc.api.APISession;
import static triggerdoc.api.ExcelHandler.writeXLSXData;
import static triggerdoc.api.ExcelHandler.writeXLSXFile;
import triggerdoc.api.ExceptionHandler;
import triggerdoc.api.TriggerDef;

/**
 *
 * @author veckardt
 */
public class TriggerDoc {

    private static APISession session;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws APIException {
        // TODO code application logic here
        session = new APISession(args, "IntegrityTriggerDoc");

        out.println("Integrity Trigger Doc - V" + programVersion + "\n----------------------------");

        String objectName = "";
        // String typeName = "";
        String groupName = "";
        String adminName = "";
        String remove = "";

        if (args.length != 0) {
            for (String arg : args) {
                objectName = getParam(arg, "name", objectName);
                // typeName = getParam(arg, "type", typeName).toLowerCase();
                groupName = getParam(arg, "group", groupName);
                adminName = getParam(arg, "admin", adminName);
                remove = getParam(arg, "remove", remove).toLowerCase();

                if (arg.indexOf("--?") == 0) {
                    usage();
                    exit(0);
                }
            }
        }
//        if (typeName.isEmpty()) {
//            log("Parameter '--type' is required!");
//            usage();
//            exit(4);
//        }
////        if (newFields.isEmpty() && copyFromTypeName.isEmpty()) {
////            log("One of the Parameters '--fields' or '--copyFromType' is required!");
////            exit(5);
////        }
//        if (objectName.isEmpty()) {
//            log("Parameter '--name' is required!");
//            usage();
//            exit(6);
//        }
//
//        if (groupName.isEmpty() && adminName.isEmpty()) {
//            log("One of the Parameters '--group' or '--admin' is required!");
//            usage();
//            exit(6);
//        }

        try {

            Map<String, List<TriggerDef>> triggerMap = new TreeMap<>();

            int row;
            Command cmd = new Command(Command.IM, "triggers");
            cmd.addOption(new Option("fields", "assign,description,frequency,lastRunTime,name,position,query,rule,runAs,script,scriptParams,scriptTiming,type"));
            // cmd.addSelection(objectName);
            Response response = session.runCommand(cmd);
            log(cmd.getApp() + " " + cmd.getCommandName() + " exit code: " + response.getExitCode());

            WorkItemIterator wit = response.getWorkItems();
            row = 7;
            while (wit.hasNext()) {
                WorkItem wi = wit.next();

                TriggerDef td = new TriggerDef(wi);

                writeTriggerList(td, row++);

                for (String type : td.typeName) {

                    if (triggerMap.containsKey(type)) {
                        triggerMap.get(type).add(td);
                    } else {
                        List<TriggerDef> triggers = new ArrayList<>();
                        triggers.add(td);
                        triggerMap.put(type, triggers);
                    }
                }
                // 
            }
            for (String typename : triggerMap.keySet()) {
                List<TriggerDef> tdl = triggerMap.get(typename);
                Collections.sort(tdl, new Comparator<TriggerDef>() {
                    @Override
                    public int compare(TriggerDef tdef1, TriggerDef tdef2) {
                        return tdef1.compName().compareTo(tdef2.compName());
                    }
                });
            }

            int col = 0;
            for (String typename : triggerMap.keySet()) {
                col = col + 2;
                writeXLSXData(1, 5, col, typename, true);
                row = 2;
                List<TriggerDef> tdl = triggerMap.get(typename);
                for (TriggerDef td : tdl) {
                    // log(td.position + ": " + td.name + ", " + td.rule + ", " + td.type + ", " + td.ruleType + " => " + td.typeName);
                    writeXLSXData(1, 3 + row * 2, col, td.position + ": " + td.name + "\n" + td.getFlags(), td.isActive);
                    row++;
                }
            }

            writeXLSXData(1, 3, 2, session.getServerInfo(), true);
            writeXLSXData(2, 3, 2, session.getServerInfo(), true);
            writeXLSXFile();

        } catch (APIException ex) {
            // log(ex.toString());
            ExceptionHandler eh = new ExceptionHandler(ex);
            log(eh.getCommand());
            log("\nERROR: " + eh.getMessage() + "\n");
            exit(-2);
        }
    }

    private static void writeTriggerList(TriggerDef td, int row) {
        int col = 1;
        // for (String typename : triggerMap.keySet()) {
            // writeXLSXData(2, row, col, typename, true);
        // List<TriggerDef> tdl = triggerMap.get(typename);
        // for (TriggerDef td : tdl) {
        log(td.position + ": " + td.name + ", " + td.rule + ", " + td.type + ", " + td.ruleType + " => " + td.typeName);
        writeXLSXData(2, row, col + 1, td.position + "", td.isActive);
        writeXLSXData(2, row, col + 2, td.name, td.isActive);
        writeXLSXData(2, row, col + 3, td.description, td.isActive);
        writeXLSXData(2, row, col + 4, td.type, td.isActive);
        writeXLSXData(2, row, col + 5, td.rule, td.isActive);
        writeXLSXData(2, row, col + 6, td.script, td.isActive);
        writeXLSXData(2, row, col + 7, td.scriptTiming, td.isActive);
        writeXLSXData(2, row, col + 8, td.scriptParams.toString(), td.isActive);
        writeXLSXData(2, row, col + 9, td.query, td.isActive);
        writeXLSXData(2, row, col + 10, td.assign, td.isActive);
        row++;
        // }
        // }
    }

    private static String getParam(String arg, String param, String defaultValue) {
        if (arg.indexOf("--" + param + "=") == 0) {
            return arg.substring(("--" + param + "=").length(), arg.length());
            // parCount++;
        }
        return defaultValue;
    }

    public static void log(String text) {
        out.println(text);
    }
}
