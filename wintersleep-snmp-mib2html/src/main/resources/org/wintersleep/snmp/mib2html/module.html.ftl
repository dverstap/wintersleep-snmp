[@layout title="${module.id}"]
<h1>${module.id}</h1>

<p>TODO: description, revisions, contact (or rather put the administrative stuff at the back?)</p>

<h2>Types</h2>

<p>TODO</p>


<h2>Scalars</h2>

<a name="scalars"></a>

<table class="table table-striped table-bordered">
    <tr>
        <th>Name</th>
        <th>OID</th>
        <th>Type</th>
    </tr>
    [#list module.scalars as scalar]
        [@variablerow scalar][/@variablerow]
    [/#list]
</table>


<h2>Tables</h2>

<a name="tables"></a>
<table class="table table-striped table-bordered">
    <tr>
        <th>Name</th>
        <th>OID</th>
        <th>#Columns</th>
    </tr>
    [#list module.tables as table]
        <tr>
            <td><a href="${table.id}.html" title="${table.description}">${table.id}</a></td>
            <td>${table.oidStr}</td>
            <td><a href="${table.id}.html#columns">${table.row.columns.size()}</a></td>
        </tr>
    [/#list]
</table>

<h2>Scalar Details</h2>

    [#list module.scalars as scalar]
        [@variable scalar][/@variable]
    [/#list]


[/@layout]
