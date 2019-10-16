 $(function() {
    $("#register").submit(function(e){
        e.preventDefault();

console.log("aaaa");
        var pubkey = $("#pubkey").html().trim();

        var encrypt = new JSEncrypt();
        encrypt.setPublicKey(pubkey);

        var exp = $("#username").val();
        if ((!exp && typeof(exp)!="undefined" && exp!=0) || exp == "")
{
alert("username不能为空");
return;
}       　
exp = $("#password").val();
if ((!exp && typeof(exp)!="undefined" && exp!=0) || exp == "")
{
alert("password不能为空");
return;
}       　
        var pwd = encrypt.encrypt($("#password").val());
        console.log($("#password").val());
        console.log(pwd);
        $.post("/register",{"username":$("#username").val(), "password":pwd},function(result){
        if (result)
            $.post("/login",{"username":$("#username").val(), "password":pwd},function(result){
                location.href = "/chat";
            });
        else
            alert("用户重复");
        });
    });

});