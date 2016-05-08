[#macro variable variable]
<a id="${variable.id}"></a>
<h2>${variable.id} (${variable.oidStr})</h2>
<pre>SYNTAX ${variable.type} (TODO improve this)
MAX-ACCESS ${variable.accessAll}
STATUS ${variable.status}
DESCRIPTION
    "${variable.description}"</pre>
[/#macro]
