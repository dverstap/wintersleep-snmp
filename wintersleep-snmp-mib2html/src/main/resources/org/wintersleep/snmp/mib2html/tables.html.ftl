[@layout title="Tables"]
<table class="table table-striped table-bordered">
    <tr>
        <th>Name</th>
        <th>Module</th>
        <th>#Columns</th>
    </tr>
    [#list tables as table]
        <tr>
            <td><a href="${table.module.id}.html#${table.id}">${table.id}</a></td>
            <td><a href="${table.module.id}.html">${table.module.id}</a></td>
            <td>${table.row.columns.size()}</td>
        </tr>
    [/#list]]
</table>
[/@layout]
