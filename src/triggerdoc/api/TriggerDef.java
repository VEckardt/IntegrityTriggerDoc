/*
 * Copyright:      Copyright 2017 (c) Parametric Technology GmbH
 * Product:        PTC Integrity Lifecycle Manager
 * Author:         Volker Eckardt, Principal Consultant ALM
 * Purpose:        Custom Developed Code
 * **************  File Version Details  **************
 * Revision:       $Revision: 1.2 $
 * Last changed:   $Date: 2017/11/18 02:18:20CET $
 */
package triggerdoc.api;

import com.mks.api.response.Item;
import com.mks.api.response.WorkItem;
import static java.lang.String.format;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author veckardt
 */
public class TriggerDef {

    public String name;
    public String type;
    public String rule;
    public String ruleType;
    public List<String> typeName = new ArrayList<>();
    public int position;
    public String script;
    public String query;
    public String scriptParams ="";
    public String scriptTiming;
    public String description;
    public String assign = "";
    public Boolean isActive = true;

    public TriggerDef(WorkItem wi) {
        name = wi.getField("name").getValueAsString();
        rule = wi.getField("rule").getValueAsString();
        type = wi.getField("type").getValueAsString();
        query = wi.getField("query").getValueAsString();
        position = wi.getField("position").getInteger();
        script = wi.getField("script").getValueAsString();
        description = wi.getField("description").getValueAsString();

        for (Object param : wi.getField("scriptParams").getList()) {
            Item item = (Item) param;
            scriptParams += scriptParams.isEmpty() ? "" : ",\n";
            scriptParams += item.getId() + "=" + item.getField("value").getValueAsString();
        }

        // scriptParams = wi.getField("scriptParams").getList();
        scriptTiming = wi.getField("scriptTiming").getValueAsString();
        // assign = wi.getField("assign").getList();
        for (Object param : wi.getField("assign").getList()) {
            Item item = (Item) param;
            assign += assign.isEmpty() ? "" : ",\n";
            assign += item.getId() + "=" + item.getField("value").getValueAsString();
        }        

        if (rule != null && rule.contains("item is segment")) {
            typeName.add("All Documents");
        } else if (rule != null && (rule.contains("item is node") || rule.contains("item is content"))) {
            typeName.add("All Document Nodes");
        } else if (rule != null && (rule.contains("[\"Type\"] =") || rule.contains("[\"Typ\"] ="))) {
            // typeName.add("Type related");
            Pattern p = Pattern.compile("field'?\\[\"Type?\"\\] = (\"[a-zA-Z ]+\")");
            Matcher m = p.matcher(rule.replaceAll("==", "="));
            while (m.find()) {
                // System.out.println();
                typeName.add(m.group(1).replace("\"", ""));
            }
        } else {
            typeName.add(type);
        }
        if (type.equals("rule")) {
            if (scriptTiming != null && !scriptTiming.equals("none")) {

            } else {
                isActive = false;
            }
        }
    }

    public String getFlags() {
        String flags = "";
        if (script != null && !script.isEmpty()) {
            flags += (flags.isEmpty() ? "" : ",") + "s";
        }
        if (!scriptParams.isEmpty()) {
            flags += (flags.isEmpty() ? "" : ",") + "p";
        }
        if (query != null && !query.isEmpty()) {
            flags += (flags.isEmpty() ? "" : ",") + "q";
        }
        if (!assign.isEmpty()) {
            flags += (flags.isEmpty() ? "" : ",") + "a";
        }
        if (scriptTiming != null && !scriptTiming.equals("none")) {
            flags += (flags.isEmpty() ? "" : ",") + scriptTiming;
        }
        if (typeName.size() > 1) {
            flags += (flags.isEmpty() ? "" : " ") + "(" + typeName.size() + ")";
        }

        return "[" + flags + "]";
    }

    public String compName() {
        String compStr = "5";
        if (type.equals("rule") || type.equals("branch")) {
            if (scriptTiming.equals("pre")) {
                compStr = "1";
            }
            if (scriptTiming.equals("post")) {
                compStr = "2";
            }
        }
        compStr += format("%08d%n", position);
        return compStr;
    }

}
