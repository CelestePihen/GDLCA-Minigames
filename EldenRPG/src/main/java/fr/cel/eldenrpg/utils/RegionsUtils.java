package fr.cel.eldenrpg.utils;

import lombok.Getter;

@Getter
public enum RegionsUtils {

    FIRST_FIRECAMP("hintfirstfirecamp", "\uE000"),
    PASS_THROUGH_BLOCK("hintpassthroughblock", "\uE001"),
    ;

    private final String name;
    private final String hint;

    RegionsUtils(String name, String hint) {
        this.name = name;
        this.hint = hint;
    }

}