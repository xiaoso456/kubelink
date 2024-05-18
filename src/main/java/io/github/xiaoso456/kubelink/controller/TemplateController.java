package io.github.xiaoso456.kubelink.controller;

import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.domain.SyncConfig;
import io.github.xiaoso456.kubelink.domain.SyncResponse;
import io.github.xiaoso456.kubelink.domain.TextTemplate;
import io.github.xiaoso456.kubelink.enums.SyncType;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import io.github.xiaoso456.kubelink.service.TextTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/text-template")
public class TemplateController {

    @Autowired
    private TextTemplateService textTemplateService;

    @GetMapping("/{id}")
    public TextTemplate get(@PathVariable Long id) {
        return textTemplateService.getById(id);
    }

    @GetMapping("/list")
    public List<TextTemplate> list() {
        return textTemplateService.list();
    }

    @PostMapping("")
    public boolean add(@RequestBody TextTemplate textTemplate) {
        return textTemplateService.save(textTemplate);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id,@RequestBody TextTemplate textTemplate) {
        textTemplate.setId(id);
        return textTemplateService.updateById(textTemplate);
    }

    @DeleteMapping("/config/{id}")
    public boolean delete(@PathVariable Long id) {
        return textTemplateService.removeById(id);
    }


}
