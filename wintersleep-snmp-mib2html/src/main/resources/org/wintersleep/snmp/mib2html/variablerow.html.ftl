[#macro variablerow variable]
<tr>
    <td><a href="#${variable.id}" title="${variable.description}">${variable.id}</a></td>
    <td>${variable.oidStr}</td>
    <td>${variable.type}</td>
    <td>${variable.accessAll}</td>
</tr>
[/#macro]
