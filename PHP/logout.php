<?php
error_reporting(0);
require_once "utils.php";
session_start();

if (isset($_SESSION['user'])) {
    unset($_SESSION['user']);
    echo '{ "result": true }';
} else
	echo '{ "result": false }';

?>