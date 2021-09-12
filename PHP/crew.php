<?php
error_reporting(0);
require_once "utils.php";
session_start();

$conn = null;
try {
	$conn = getConnection();
    if ($_SERVER['REQUEST_METHOD'] == 'GET') {
        if ($_GET['movie_id'] != null) {
			$res = pg_query_params($conn, "SELECT * FROM get_film_crew($1)", array($_GET['movie_id']));
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
				//member_id int4, member_surname varchar, member_name varchar, member_patronymic varchar, crew_role_name varchar
				$resJSON .= '"id": "' . $row[0] . '",';
				$resJSON .= '"surname": "' . $row[1] . '",';
				$resJSON .= '"name": "' . $row[2] . '",';
				$resJSON .= '"patronymic": "' . $row[3] . '",';
				$resJSON .= '"role": "' . $row[4] . '",';
				$resJSON .= '"editable": ' . ($editable == true ? 'true' : 'false');
				$resJSON .= '}';
			}
			$resJSON .= ']';
			echo $resJSON;
		} else throw new Exception("Фильм не указан!");
    } elseif ($_SERVER['REQUEST_METHOD'] == 'POST') {
        if (!isset($_SESSION['user']))
            throw new Exception("Вы не авторизированы!");
		if ($_SESSION['user']->hasAccess() != true)
			throw new Exception('Нет доступа!');
		if ($_POST['arg'] != null) {
			if(json_decode($_POST['arg']->id) == 0)
				$res = pg_query_params($conn, 'SELECT * FROM new_crew_member($1)', array(rawurldecode($_POST['arg'])));
			else
				$res = pg_query_params($conn, 'SELECT * FROM update_crew_member($1)', array(rawurldecode($_POST['arg'])));
			if(!$res)
				displayError(pg_last_error($conn));
			else {
				if(json_decode($_POST['arg'])->id == 0)
					echo '{ "result": ', pg_fetch_result($res, 0, 0), '}';
				else
					echo '{ "result": true }';
			}
		} else if($_GET['set'] != null && $_GET['id'] != null) {
			$res = pg_query_params($conn, 'SELECT * FROM set_member($1, $2)', array($_GET['id'], $_GET['set']));
			if(!$res)
				displayError(pg_last_error($conn));
			else
				echo '{ "result": true }';
		} else if($_GET['unset'] != null && $_GET['id'] != null) {
			$res = pg_query_params($conn, 'SELECT * FROM unset_member($1, $2)', array($_GET['id'], $_GET['unset']));
			if(!$res)
				displayError(pg_last_error($conn));
			else
				echo '{ "result": true }';
		} else throw new Exception("Аргументы не заданы!");
    } elseif ($_SERVER['REQUEST_METHOD'] == 'DELETE') {
        if (!isset($_SESSION['user']))
            throw new Exception("Вы не авторизированы!");
		if ($_SESSION['user']->hasAccess() != true)
			throw new Exception('Нет доступа!');
		if ($_GET['id'] != null) {
			$res = pg_query_params($conn, 'SELECT * FROM del_crew_member($1)', array($_GET['id']));
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