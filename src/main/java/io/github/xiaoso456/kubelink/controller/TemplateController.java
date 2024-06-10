package io.github.xiaoso456.kubelink.controller;

import cn.hutool.core.util.StrUtil;
import io.github.xiaoso456.kubelink.domain.TextTemplate;
import io.github.xiaoso456.kubelink.service.TextTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<TextTemplate> list(@RequestParam(required = false) String search,@RequestParam(required = false) String type) {
        List<TextTemplate> textTemplateList = textTemplateService.list();
        if(StrUtil.isNotBlank(search)){
            textTemplateList = textTemplateList.stream()
                    .filter(t -> t.getName().contains(search) || t.getContent().contains(search) || t.getDescription().contains(search))
                    .collect(Collectors.toList());
        }
        if(StrUtil.isNotBlank(type)){
            textTemplateList = textTemplateList.stream()
                    .filter(t -> t.getType().equals(type))
                    .collect(Collectors.toList());
        }
        return textTemplateList;
    }

    @GetMapping("/type/list")
    public List<String> listType() {
        return textTemplateService.list().stream()
                .map(t -> t.getType())
                .distinct()
                .collect(Collectors.toList());
    }

    @PostMapping("")
    public boolean add(@RequestBody TextTemplate textTemplate) {
        textTemplate.setId(null);
        return textTemplateService.save(textTemplate);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id,@RequestBody TextTemplate textTemplate) {
        textTemplate.setId(id);
        return textTemplateService.updateById(textTemplate);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return textTemplateService.removeById(id);
    }


}
