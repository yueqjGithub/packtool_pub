package com.avalon.packer.datarepo;

import lombok.Data;
import java.util.Set;

@Data
public class SourceRepoItem {
    public Integer length;
    public Set<Integer> already;

    public void addAlready (Integer idx) {
        already.add(idx);
    }

    public boolean checkAlready (Integer idx) {
        return already.contains(idx);
    }
}
