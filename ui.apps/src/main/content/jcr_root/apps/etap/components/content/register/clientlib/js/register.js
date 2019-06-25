$(function() {
	$("#registerButton").click(function(e) {
        
        e.preventDefault();
    	$.ajax({
				type : "POST",
				url : "/bin/registerServlet",
				data : {
					username : $("#inputUsername").val(),
					password : $("#inputPassword").val(),
                    firstName : $("#inputFirstName").val(),
                    lastName : $("#inputLastName").val(),
					groupName : "etap-users"
				},
				success : function(data, textStatus, jqXHR) {
					$("#inputPassword").val("");
					$("#info").text(data);
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
                    $("#info").text("Error: " + textStatus);
				}
			});
    });
});

// Function to redirect to homepage after login
var getRedirectPath = function() {
	return "/content/etap/RegisterPage.html";
}