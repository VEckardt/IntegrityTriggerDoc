/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package triggerdoc.api;

import com.mks.api.Command;
import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Item;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.mks.api.response.WorkItemIterator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author veckardt
 */
public class TypeDetails {
    // Map<Float,String> mySortedMap = new TreeMap<Float,MyObject>();    

    private APISession session;
    private String typeName;
    public Map<String, String> sortedFields = new TreeMap<String, String>();
    String fieldGroup = "visibleFields";
    String fieldList = "";

    public TypeDetails(APISession session, String typeName) {
        this.typeName = typeName;
        this.session = session;
        // }

        // public String getCurrentFieldList(String fieldGroup) {
        // String fieldList = "";
        try {
            Command cmd = new Command(Command.IM, "types");
            cmd.addOption(new Option("fields", fieldGroup));
            cmd.addSelection(typeName);
            Response response = session.runCommand(cmd);
            WorkItemIterator wit = response.getWorkItems();
            while (wit.hasNext()) {
                WorkItem wi = wit.next();
                // Field visibleFields = ;
                for (Object object : wi.getField(fieldGroup).getList()) {
                    Item item = (Item) object;
                    fieldList = fieldList + item.getId() + ":";
                    // Field groups = item.getField("groups");
                    String grps = "";
                    for (Object groups : item.getField("groups").getList()) {
                        // System.out.println(((Item)groups).getId());

                        if (!fieldList.endsWith(":")) {
                            fieldList = fieldList + ",";
                        }
                        fieldList = fieldList + ((Item) groups).getId();
                        grps = grps + (grps.isEmpty() ? "" : ",") + ((Item) groups).getId();
                    }
                    fieldList = fieldList + ";";
                    sortedFields.put(item.getId(), grps);
                };

            }
        } catch (APIException ex) {
            // System.out.println(ex.toString());
            ExceptionHandler eh = new ExceptionHandler(ex);
            System.out.println(eh.toString());
            System.exit(-1);
        }
        // System.out.println(fieldList);
        //  return fieldList;
    }

    public String getFieldList() {
        return fieldList;
    }

    public String getTypeName() {
        return this.typeName;
    }
}
