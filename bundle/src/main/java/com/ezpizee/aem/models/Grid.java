package com.ezpizee.aem.models;

import com.ezpizee.aem.utils.DataUtil;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grid extends BaseModel {

    private List<List<Map<String, String>>> data;

    @Override
    protected void exec() {
        List<Map<String, Object>> grids = new ArrayList<>();
        DataUtil.loadList(grids, getResource().getChild("grids"), "grids");

        if (!grids.isEmpty()) {
            data = new ArrayList<>();
            for (int i = 0; i < grids.size(); i++) {
                List<Map<String, String>> col = new ArrayList<>();
                Map<String, Object> ele = grids.get(i);
                String text = ele.getOrDefault("text", StringUtils.EMPTY).toString();
                if (StringUtils.isNotEmpty(text)) {
                    String[] arr = text.trim().split("\\+");
                    for (int j = 0; j < arr.length; j++) {
                        Map<String, String> column = new HashMap<>();
                        column.put("span", arr[j]);
                        column.put("resPath", "grid-"+i+"-"+j);
                        col.add(column);
                    }
                    data.add(col);
                }
            }
        }
    }

    public List<List<Map<String, String>>> getData() { return data; }
}
