<!DOCTYPE html>
<html lang='zh-CN'>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<head>
    <title>${subject!}</title>
    <style>
        th {
            white-space: nowrap;
        }
    </style>
</head>
<body>
<div style="line-height: 180%;">
    您好：<br>
    ${content!}
    <table border="1" cellspacing="0">
        <thead>
        <tr>
            <th>姓名</th>
            <th>生日</th>
            <th>年龄</th>
            <th>性别</th>
            <th>地址</th>
            <th>密码</th>
            <th>手机号</th>
            <th>薪水</th>
            <th>状态</th>
        </tr>
        </thead>
        <tbody>
        <#list userList as list>
        <tr>
            <td>${list.username!}</td>
            <td>${list.birthday?string('yyyy-MM-dd')!}</td>
            <#--<td <#if !dataCheckResult.success> style="color: red" </#if>>${dataCheckResult.success?string("是","否")}</td>-->
            <td>${list.age!}</td>
            <td>${list.sex!}</td>
            <td>${list.address!}</td>
            <td>${list.password!}</td>
            <td>${list.mobile!}</td>
            <td>${list.money!}</td>
            <td>${list.status!}</td>
        </tr>
        </#list>
        </tbody>
    </table>
</div>
</body>
</html>