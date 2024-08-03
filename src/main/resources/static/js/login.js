$.validator.setDefaults({
    submitHandler: function () {
        login();
    }
});

function login() {
    $.modal.loading($("#btnSubmit").data("loading"));
    var username = $.common.trim($("input[name='username']").val());
    var password = $.common.trim($("input[name='password']").val());
    $.ajax({
        type: "post",
        url: ctx + "login",
        data: {
            "username": username,
            "password": password,
        },
        success: function (r) {
            console.log("adadad")
            if (r.code === web_status.SUCCESS) {
                window.location.replace(ctx);
            } else {
                $('.imgcode').click();
                $(".code").val("");
                $.modal.msg(r.msg);
            }
            $.modal.closeLoading();
        }
    });
}