package ru.youngsmoke.j2c.xml;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "j2c")
public class Config {
    @ElementList(name = "targets")
    private List<String> targets;

    @ElementList(name = "include", type = Match.class, required = false)
    private List<Match> includes;

    @ElementList(name = "exclude", type = Match.class, required = false)
    private List<Match> excludes;

    public List<String> getTargets() {
        return targets;
    }

    public List<Match> getIncludes() {
        return includes;
    }

    public List<Match> getExcludes() {
        return excludes;
    }

}
