<?php
error_reporting(0);
require_once "utils.php";
session_start();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $conn = null;
    try {
		if(!isset($_POST['arg']) || $_POST['arg'] == null)
			throw new Exception('Укажите данные для авторизации!');
        $conn = getConnection();
		$argArray = json_decode($_POST['arg']);
		if($argArray->role) {
			if(!isset($_SESSION['user']) || $_SESSION['user']->isAdmin() != true) {
				unset($argArray->role);
				$_POST['arg'] = json_encode($argArray, JSON_UNESCAPED_UNICODE);
			}
		}
		$res = pg_query_params($conn, "SELECT register($1)", array(rawurldecode($_POST['arg'])));
        if(!$res)
			displayError(pg_last_error($conn));
		else
			echo '{ "result": true }';
    } catch (Exception $e) {
        echo '{ "error": "Ошибка: ', $e->getMessage(), '" }';
    } finally {
        if ($conn)
            pg_close($conn);
    }
}

?>