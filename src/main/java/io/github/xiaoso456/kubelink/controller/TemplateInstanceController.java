package io.github.xiaoso456.kubelink.controller;

import io.github.xiaoso456.kubelink.domain.TextTemplate;
import io.github.xiaoso456.kubelink.domain.TextTemplateInstance;
import io.github.xiaoso456.kubelink.service.TextTemplateInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/text-template-instance")
public class TemplateInstanceController {

    @Autowired
    private TextTemplateInstanceService textTemplateInstanceService;

    @GetMapping("/{id}")
    public TextTemplateInstance get(@PathVariable Long id) {
        return textTemplateInstanceService.getById(id);
    }

    @GetMapping("/list")
    public List<TextTemplateInstance> list() {
        return textTemplateInstanceService.list();
    }

    @PostMapping("")
    public boolean add(@RequestBody TextTemplateInstance textTemplateInstance) {
        return textTemplateInstanceService.save(textTemplateInstance);
    }

    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id,@RequestBody TextTemplateInstance textTemplateInstance) {
        textTemplateInstance.setId(id);
        return textTemplateInstanceService.updateById(textTemplateInstance);
    }

    @DeleteMapping("/config/{id}")
    public boolean delete(@PathVariable Long id) {
        return textTemplateInstanceService.removeById(id);
    }
    

}
