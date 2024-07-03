package com.inspirage.ilct.util;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SingleTonClass {

   private List<String> colorCodes=new LinkedList<>();
   private List<String> usedColorCodes = new ArrayList<>();

    public SingleTonClass() {
    }

    public List<String> getColorCodes() {
        return this.colorCodes;
    }

    public void setColorCodes(List<String> colorCodes) {
        this.colorCodes = colorCodes;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof SingleTonClass)) return false;
        final SingleTonClass other = (SingleTonClass) o;
        if (!other.canEqual(this)) return false;
        final Object this$colorCodes = this.getColorCodes();
        final Object other$colorCodes = other.getColorCodes();
        return this$colorCodes == null ? other$colorCodes == null : this$colorCodes.equals(other$colorCodes);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $colorCodes = this.getColorCodes();
        result = result * PRIME + ($colorCodes == null ? 43 : $colorCodes.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof SingleTonClass;
    }

    public String toString() {
        return "SingleTonClass(colorCodes=" + this.getColorCodes() + ")";
    }

    public void resetColorCodes() {
        usedColorCodes = new ArrayList<>();
    }
    public String getNewColorCode(String restrictedColor) {
        String code = Utility.getRandomColorCode();
        if (!usedColorCodes.contains(code) && (restrictedColor == null || !code.equals(restrictedColor))) {
            this.usedColorCodes.add(code);
            return code;
        }
        return this.getNewColorCode(restrictedColor);
    }
}
