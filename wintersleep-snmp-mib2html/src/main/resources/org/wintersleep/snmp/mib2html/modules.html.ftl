[@layout title="Modules"]
<table class="table table-striped table-bordered">
    <tr>
        <th>Name</th>
        <th>#Scalars</th>
        <th>#Tables</th>
    </tr>
    [#list modules as module]
        <tr>
            <td><a href="${module.id}.html">${module.id}</a></td>
            <td><a href="${module.id}.html#scalars">${module.scalars.size()}</a></td>
            <td><a href="${module.id}.html#tables">${module.tables.size()}</a></td>
        </tr>
    [/#list]
</table>
[/@layout]
