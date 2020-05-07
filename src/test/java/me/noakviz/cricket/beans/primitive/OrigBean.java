package me.noakviz.cricket.beans.primitive;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.noakviz.cricket.beans.CopyProperty;

@Getter
@Setter
@ToString
public class OrigBean {
    private boolean usable;
    @CopyProperty(alias = {"age1"})
    private int age;
    @CopyProperty(alias = {"age1"})
    private Integer width;
    private long height;
    private String name;
}
