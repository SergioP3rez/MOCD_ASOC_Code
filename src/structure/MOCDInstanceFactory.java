package structure;

import grafo.optilib.structure.InstanceFactory;

public class MOCDInstanceFactory extends InstanceFactory<MOCDInstance> {
    @Override
    public MOCDInstance readInstance(String s) {
        return new MOCDInstance(s);
    }
}
