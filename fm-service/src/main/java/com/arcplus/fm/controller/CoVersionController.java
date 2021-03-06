package com.arcplus.fm.controller;

import com.arcplus.fm.entity.dto.BaseResponse;
import com.arcplus.fm.entity.dto.PageEntity;
import com.arcplus.fm.common.Result;
import com.arcplus.fm.common.ResultGenerator;
import com.arcplus.fm.entity.CoVersion;
import com.arcplus.fm.service.CoVersionService;
import com.arcplus.fm.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("coVersion")
public class CoVersionController  {
    @Autowired
    CoVersionService instCoVersionService;

    @PostMapping(value = "/add")
    public Result addCoVersion(@RequestBody CoVersion entity) throws Exception {
        return ResultGenerator.genSuccessResult(instCoVersionService.save(entity));
    }

    @PostMapping(value = "/update")
    public Result updateCoVersion(@RequestBody CoVersion entity) throws Exception {
        instCoVersionService.update(entity);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping(value = "/del/{id}")
    public Result delCoVersionService(@PathVariable("id") int id) throws Exception {
        instCoVersionService.delete(id);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping(value = "")
    public Result selectAllPage(@RequestParam(value="page") int page,@RequestParam(value="limit") int limit) throws Exception {
        PageInfo<CoVersion> reslut = instCoVersionService.selectAllPage(page,limit);
        return ResultGenerator.genSuccessPageResult(reslut);
    }


    @GetMapping(value = "/{id}")
    public Result getById(@PathVariable("id") int id) throws Exception {
        return ResultGenerator.genSuccessResult(instCoVersionService.getById(id));
    }

    @PostMapping("/search")
    public BaseResponse selectAllPage(
            @RequestBody PageEntity<Map<String, Object>> obj
    ) {
        Map<String, Object> param = new HashMap<>();
        param.put("page", obj.getPageIndex());
        param.put("limit", obj.getPageSize());
        if (!CollectionUtils.isEmpty(obj.getCheckModelData())) {
            for (Map.Entry<String, Object> item : obj.getCheckModelData().entrySet()) {
                if ("fromDate".equals(item.getKey()) || "toDate".equals(item.getKey())) {
                    param.put(item.getKey(),  LocalDateTime.parse(item.getValue().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    continue;
                }
                param.put(item.getKey(), "$like." + item.getValue());
            }
        }
        if (obj.getOrderModelData().length > 0 && obj.getOrderModelData()[0].getMethod() != null) {
            List<String> list = new ArrayList<>();
            for (PageEntity.OrderObj x : obj.getOrderModelData()) {
                String end = x.getField() + "." + x.getMethod().replaceAll("end", "");
                list.add(end);
            }
            String orderStr = StringUtils.arrayToCommaDelimitedString(
                    list.toArray()
            );
            if (!StringUtils.isEmpty(orderStr)) {
                param.put("orderBy", orderStr);
            }
        }

        // Pagable
        Integer page = Integer.parseInt(param.getOrDefault("page", "1").toString());
        Integer limit = Integer.parseInt(param.getOrDefault("limit", "100").toString());
        param.remove("page");
        param.remove("limit");


        Map<String, String[]> inSearch = new HashMap<>();
        Map<String, String> fuzzySearch = new HashMap<>();

        String[] keys = new String[]{};
        keys = param.keySet().toArray(keys);
        for (String key : keys) {

            if (StringUtils.isEmpty(param.getOrDefault(key, "").toString())) {
                param.remove(key);
                continue;
            }
            String value = param.get(key).toString();

            // $in
            if (value.startsWith("$in.")) {
                String sqlCloumnN = StringHelper.HumpToUnderline(key);
                if (null != sqlCloumnN) {
                    inSearch.put(sqlCloumnN, value.substring(4).split(","));
                }
                param.remove(key);
                continue;
            }

            // $like
            if (value.startsWith("$like.")) {
                String sqlCloumnN = StringHelper.HumpToUnderline(key);
                if (null != sqlCloumnN) {
                    fuzzySearch.put(sqlCloumnN, String.format("%%%s%%", value.substring(6)));
                }
                param.remove(key);
                continue;
            }
        }
        if (!fuzzySearch.isEmpty()) {
            param.put("fuzzySearch", fuzzySearch);
        }
        if (!inSearch.isEmpty()) {
            param.put("inSearch", inSearch);
        }

        // Order
        if (param.containsKey("orderBy")) {
            param.replace(
                    "orderBy",
                    String.join(
                            ",",
                            Arrays.stream(param.get("orderBy").toString().split(","))
                                    .map((e -> {
                                        String[] kv = e.split("\\.");
                                        kv[0] = StringHelper.HumpToUnderline(kv[0]);
                                        return String.join(" ", kv);
                                    }))
                                    .toArray(String[]::new)));
        } else {
            param.put("orderBy", "version_id desc");
        }
        PageInfo<CoVersion> reslut = instCoVersionService.search(param, page, limit);
        return ResultGenerator.genSucessPageResult(reslut);
    }



}