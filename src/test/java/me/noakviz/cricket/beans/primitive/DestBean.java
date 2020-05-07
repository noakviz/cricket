package me.noakviz.cricket.beans.primitive;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.noakviz.cricket.beans.CopyProperty;

@Getter
@Setter
@ToString
public class DestBean {
    private boolean usable;
    @CopyProperty(alias = {"age"})
    private int age1;
    private Integer width;
    private Integer height;
    private String name;
}
