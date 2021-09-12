<?php
error_reporting(0);
require_once "utils.php";
session_start();

$conn = null;
try {
	$conn = getConnection();
	if ($_SERVER['REQUEST_METHOD'] == 'GET') {
		if($_GET['check'] == null) {
			if(isset($_GET['arg']) && $_GET['arg'] != null) {
				$argArray = json_decode($_GET['arg']);
				if(isset($argArray->reserved)) {
					if(isset($_SESSION['user'])) {
						$argArray->reserved = $_SESSION['user']->getLogin();
						$_GET['arg'] = json_encode($argArray, JSON_UNESCAPED_UNICODE);
					} else
						throw new Exception('Вы не авторизированы!');
				}
			}
			$res = pg_query_params($conn, "SELECT * FROM view_tickets($1, $2, $3)",
				array($_GET['arg'] ? rawurldecode($_GET['arg']) : '{}', $_GET['page'], $_GET['pageSize']));
			if(!$res)
				displayError(pg_last_error($conn));
			$editable = false;
			if(isset($_SESSION['user']))
				$editable = $_SESSION['user']->hasAccess();
			$resJSON = '[';
			while ($row = pg_fetch_row($res)) {
				if($resJSON != '[')
					$resJSON .= ',';
				$resJSON .= '{';
				$resJSON .= '"id": "' . $row[0] . '",';
				$resJSON .= '"movieId": "' . $row[1] . '",';
				$resJSON .= '"begining": "' . $row[2] . '",';
				$resJSON .= '"hall": "' . $row[3] . '",';
				$resJSON .= '"title": "' . $row[4] . '",';
				$resJSON .= '"releaseYear": "' . $row[5] . '",';
				$resJSON .= '"count": "' . $row[6] . '",';
				$resJSON .= '"cost": "' . $row[7] . '",';
				$resJSON .= '"editable": ' . ($editable == true ? 'true' : 'false');
				$resJSON .= '}';
			}
			$resJSON .= ']';
		} else {
			if (!isset($_SESSION['user']))
				throw new Exception("Вы не авторизированы!");
			$res = pg_query_params($conn, "SELECT * FROM check_reserved($1, $2)", 
				array($_SESSION['user']->getLogin(), $_GET['check']));
			if(!$res)
				displayError(pg_last_error($conn));
			$resJSON = '[{"reservedId": "' . pg_fetch_result($res, 0, 0) . '"}]';
		}
		echo $resJSON;
    } elseif ($_SERVER['REQUEST_METHOD'] == 'POST') {
        if (!isset($_SESSION['user']))
            throw new Exception("Вы не авторизированы!");
		if($_GET['reserve'] == null && $_GET['unreserve'] == null) {
			if ($_SESSION['user']->hasAccess() != true)
				throw new Exception('Нет доступа!');
			if ($_POST['arg'] != null) {
				if(json_decode($_POST['arg']->id) == 0)
					$res = pg_query_params($conn, 'SELECT * FROM new_ticket($1)', array(rawurldecode($_POST['arg'])));
				else
					$res = pg_query_params($conn, 'SELECT * FROM update_ticket($1)', array(rawurldecode($_POST['arg'])));
				if(!$res)
					displayError(pg_last_error($conn));
				else {
					if(json_decode($_POST['arg'])->id == 0)
						echo '{ "result": ', pg_fetch_result($res, 0, 0), '}';
					else
						echo '{ "result": true }';
				}
			} else throw new Exception("Аргументы не заданы!");
		} else if($_GET['unreserve'] != null){
			$res = pg_query_params($conn, 'SELECT * FROM unreserve($1)', array($_GET['unreserve']));
			if(!$res)
				displayError(pg_last_error($conn));
			else
				echo '{ "result": true }';
		} else if($_GET['reserve'] != null) {
			$res = pg_query_params($conn, 'SELECT * FROM reserve($1, $2)', array($_GET['reserve'], $_SESSION['user']->getLogin()));
			if(!$res)
				displayError(pg_last_error($conn));
			else
				echo '{ "result": ', pg_fetch_result($res, 0, 0), ' }';
		} else throw new Exception('Выберите действие!');
    } elseif ($_SERVER['REQUEST_METHOD'] == 'DELETE') {
        if (!isset($_SESSION['user']))
            throw new Exception("Вы не авторизированы!");
		if ($_SESSION['user']->hasAccess() != true)
			throw new Exception('Нет доступа!');
		if ($_GET['id'] != null) {
			$res = pg_query_params($conn, 'SELECT * FROM del_ticket($1)', array($_GET['id']));
			if(!$res)
				displayError(pg_last_error($conn));
			else
				echo '{ "result": true }';
		} else throw new Exception("Выберите удаляемую запись!");
    }
} catch (Exception $e) {
    echo '{ "error": "Ошибка: ', $e->getMessage(), '" }';
} finally {
    if ($conn)
        pg_close($conn);
}
?>