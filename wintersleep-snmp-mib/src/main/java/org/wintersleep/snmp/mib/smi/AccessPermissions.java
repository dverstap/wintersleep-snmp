package org.wintersleep.snmp.mib.smi;

public interface AccessPermissions {
    boolean isReadable();
    boolean isWritable();
    boolean isCreateWritable();    
}
