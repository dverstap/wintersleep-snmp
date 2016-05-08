[@layout title="${table.id}"]

<p>TODO: link back to module</p>

<h1> ${table.id} (${table.oidStr})</h1>

<pre>${table.description}</pre>

<h1>${table.row.id} (${table.row.oidStr})</h1>

<pre>${table.row.description}</pre>

<h1>Indexes</h1>

<table class="table table-striped table-bordered">
    <tr>
        <th>Name</th>
        <th>OID</th>
        <th>Type</th>
        <th>Access</th>
    </tr>
[#--TODO: indexes should not never return null, this is because the core library doesn't deal well with AUGMENTS --]
[#--TODO: handle IMPLIED --]
    [#if table.row.indexes??]
        [#list table.row.indexes as index]
            [@variablerow index.column][/@variablerow]
        [/#list]
    [/#if]
</table>

<h1>Column Overview</h1>

<table class="table table-striped table-bordered">
    <tr>
        <th>Name</th>
        <th>OID</th>
        <th>Type</th>
        <th>Access</th>
    </tr>
    [#list table.row.columns as column]
        [@variablerow column][/@variablerow]
    [/#list]
</table>

<h1>Column Details</h1>

<p>TODO: links back up to the top</p>

    [#list table.row.columns as column]
        [@variable column][/@variable]
    [/#list]

[/@layout]
