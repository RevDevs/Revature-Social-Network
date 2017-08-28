let app = angular.module("loginPageApp", ["ngRoute"]);

app.run(function($rootScope, $http, $location){
	$rootScope.status = "";
	
	$rootScope.login = function(username,password){
		$http({
			method: 'POST',
			url: '/authentication/login',
			data: 'username='+username+"&password="+password,
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).then(function successCallback(response){
			let success = response.data.success;
			if(success){
				window.location.href = "/home";
			} else {
				$rootScope.status= response.data.message;
			}
		}, function errorCallback(response){
			$rootScope.status = response.data.error + ": " + response.data.exception;
		});
	};
	
	//Log in if the user has a session
	$rootScope.autoLogin = function(){
		$http({
			method: 'GET',
			url: '/authentication/user',
		}).then(function successCallback(response){
			let success = response.data.success;
			if(success){
				window.location.href = "/home";
			}
		}, function errorCallback(response){
		});
	};
	
	$rootScope.autoLogin();
});

app.controller("registerController", function($scope,$rootScope,$http,$location){
	$scope.status = "";
	$scope.statusColor = "red";
	$scope.register = function(username,password,email,fname,lname){
		if(!username || !password || !email || !fname || !lname){
			$scope.status = "You got empty fields";
			$scope.statusColor = "red";
			return;
		}
		
		$http({
			method: 'POST',
			url: '/employees',
			data: 'employeeId=0&username='+username+"&password="+password+"&email="+email+"&fname="+fname+"&lname="+lname,
			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).then(function successCallback(response){
			let success = response.data.success;
			if(success){
				$scope.status = username + " created";
				$scope.statusColor = "green";
			} else {
				$scope.status = response.data.message;
				$scope.statusColor = "red";
			}
		}, function errorCallback(response){
			$scope.status = response.data.error + ": " + response.data.exception;
			$scope.statusColor = "red";
		});
	};
});