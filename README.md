[![Maven Central](https://img.shields.io/maven-central/v/org.wintersleep.snmp/wintersleep-snmp.svg)]()
[![License](https://img.shields.io/github/license/dverstap/wintersleep-snmp.svg)]()

Wintersleep SNMP
================

This is a Java library for parsing SNMP mib files
([SMI](https://en.wikipedia.org/wiki/Structure_of_Management_Information)).

It builds a completely cross-referenced and type-safe symbol table.

There are tools to:

- generate Java code from the SNMP mib.

- generate HTML documentation from the SNMP mibs (work in progress).


TODO
----

- Remove dependency on commons-collections: perhaps use Guava
  instead. See GenMultiMap for instance.

- Remove dependency on commons-beanutils.

- JSR305 annotations

- Upgrade SNMP mibs from `libsmi`.

- Integrate xdoc(s) (there's two of them) and remove them.

- Fix deployment to Maven Central.

- Add extensive mib parsing integration tests, for example from:

  - https://github.com/librenms/librenms/tree/master/mibs

- Port tests to JUnit 4 style (annotations instead of subclassing)

- Cleanup URLListFactory: immutable, builder classes, ... Path instead
  of File, ...


BUGS
----

adsl2LineTable.html: there are many columns with type 'null'.

