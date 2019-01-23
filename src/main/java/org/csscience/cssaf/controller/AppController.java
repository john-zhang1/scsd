/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csscience.cssaf.controller;

import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import org.csscience.cssaf.content.State;
import org.csscience.cssaf.content.Zip;
import org.csscience.cssaf.service.StateService;
import org.csscience.cssaf.service.ZipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
@ComponentScan("org.csscience.cssaf")
public class AppController {

    @Autowired
    private StateService stateService;

    @Autowired
    private ZipService zipService;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = { "/", "/statelist" }, method = RequestMethod.GET)
    public String listStates(ModelMap model) {

        List<State> states = stateService.findAll();
        model.addAttribute("states", states);
        return "allstates";
    }

    @RequestMapping(value = { "/new" }, method = RequestMethod.GET)
    public String newState(ModelMap model) {
        State state = new State();
        model.addAttribute("state", state);
        model.addAttribute("edit", false);
        return "stateRegistration";
    }

    @RequestMapping(value = { "/new"}, method = RequestMethod.POST)
    public String saveState(@Valid State state, BindingResult result, ModelMap model){
        if(result.hasErrors()){
            return "stateRegistration";
        }

        if(!stateService.isShortNameUnique(state.getShortName())){
            FieldError stateError =new FieldError("state", "shortName", messageSource.getMessage("non.unique.shortName", new String[]{state.getShortName()}, Locale.getDefault()));
            result.addError(stateError);
            return "stateRegistration";
        }

        if(!stateService.isLongNameUnique(state.getLongName())){
            FieldError stateError =new FieldError("state", "longName", messageSource.getMessage("non.unique.longName", new String[]{state.getLongName()}, Locale.getDefault()));
            result.addError(stateError);
            return "stateRegistration";
        }

        stateService.saveState(state);

        model.addAttribute("success", "State " + state.getLongName() + " registered successfully");
        return "success";
    }

    @RequestMapping(value = { "/edit-{stateId}-state" }, method = RequestMethod.GET)
    public String editState(@PathVariable int stateId, ModelMap model) {
        State state = stateService.findById(stateId);
        model.addAttribute("state", state);
        model.addAttribute("edit", true);
        return "stateRegistration";
    }

    @RequestMapping(value = { "/edit-{stateId}-state" }, method = RequestMethod.POST)
    public String updateState(@Valid State state, BindingResult result,
            ModelMap model, @PathVariable int stateId) {
 
        if (result.hasErrors()) {
            return "stateRegistration";
        }

        if(!stateService.isShortNameUnique(state.getShortName())){
            FieldError stateError =new FieldError("state", "shortName", messageSource.getMessage("non.unique.shortName", new String[]{state.getShortName()}, Locale.getDefault()));
            result.addError(stateError);
            return "stateRegistration";
        }

        stateService.updateState(state);;

        model.addAttribute("success", "State " + state.getLongName() + " updated successfully");
        return "success";
    }

    @RequestMapping(value = { "/delete-{shortName}-state" }, method = RequestMethod.GET)
    public String deleteState(@PathVariable String shortName) {
        stateService.deleteStateByShortName(shortName);
        return "redirect:/statelist";
    }

    @RequestMapping(value = { "/ziplist" }, method = RequestMethod.GET)
    public String listZips(ModelMap model) {

        List<Zip> zips = zipService.findAll();
        model.addAttribute("zips", zips);
        return "zips";
    }

    @RequestMapping(value = { "/newzip" }, method = RequestMethod.GET)
    public String newZip(ModelMap model) {
        Zip zip = new Zip();
        model.addAttribute("zip", zip);
        model.addAttribute("edit", false);
        return "zipRegistration";
    }

    @RequestMapping(value = { "/newzip"}, method = RequestMethod.POST)
    public String saveZip(@Valid Zip zip, BindingResult result, ModelMap model){
        if(result.hasErrors()){
            return "zipRegistration";
        }

        if(!zipService.isZipcodeUnique(zip.getZip())){
            FieldError zipError =new FieldError("zip", "zip", messageSource.getMessage("non.unique.zipcode", new String[]{zip.getZip()}, Locale.getDefault()));
            result.addError(zipError);
            return "zipRegistration";
        }

        zipService.saveZip(zip);

        model.addAttribute("success", "Zip " + zip.getZip() + " registered successfully");
        return "success";
    }

    @RequestMapping(value = { "/edit-{zipcode}-zip" }, method = RequestMethod.GET)
    public String editZip(@PathVariable String zipcode, ModelMap model) {
        Zip zip = zipService.findByZip(zipcode);
        model.addAttribute("zip", zip);
        model.addAttribute("edit", true);
        return "zipRegistration";
    }

    @RequestMapping(value = { "/edit-{zipcode}-zip" }, method = RequestMethod.POST)
    public String updateZip(@Valid Zip zip, BindingResult result,
            ModelMap model, @PathVariable String zipcode) {
 
        if (result.hasErrors()) {
            return "zipRegistration";
        }

        if(!zipService.isZipcodeUnique(zip.getZip())){
            FieldError zipError =new FieldError("zip", "zip", messageSource.getMessage("non.unique.zipcode", new String[]{zip.getZip()}, Locale.getDefault()));
            result.addError(zipError);
            return "zipRegistration";
        }

        zipService.updateZip(zip);;

        model.addAttribute("success", "State " + zip.getZip() + " updated successfully");
        return "success";
    }

    @RequestMapping(value = { "/delete-{zipcode}-zip" }, method = RequestMethod.GET)
    public String deleteZip(@PathVariable String zipcode) {
        zipService.deleteZipByZipcode(zipcode);
        return "redirect:/ziplist";
    }

}
