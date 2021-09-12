<?php

if(isset($_POST['req'])) {
	echo $_POST['req'];
	if(isset(json_decode($_POST['req'])->id)) {
		echo "<br>", json_decode($_POST['req'])->id;
		$arr = json_decode($_POST['req']);
		$arr->test = 'test1';
		$_POST['req'] = json_encode($arr, JSON_UNESCAPED_UNICODE);
		echo "<br>", $_POST['req'];
	}
	else
		echo "\nPizdeshn...";
} else
	echo "Pizdeshn...";

?>