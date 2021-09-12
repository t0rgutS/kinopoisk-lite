<?php
error_reporting(0);
require_once "utils.php";
session_start();

$conn = null;
try {
	$conn = getConnection();
    if ($_SERVER['REQUEST_METHOD'] == 'GET') {
		if (!isset($_SESSION['user']))
            throw new Exception("Вы не авторизированы!");
        $res = pg_query_params($conn, "SELECT * FROM get_user_data($1)",
                array($_SESSION['user']->getLogin()));
		if(!$res)
			displayError(pg_last_error($conn));
        $resJSON = '[';
        while ($row = pg_fetch_row($res)) {
			if($resJSON != '[')
				$resJSON .= ',';
			//visitor_id, visitor_login, visitor_surname, visitor_name, visitor_patronymic, birth_date, register_date
			$resJSON .= '{';
			$resJSON .= '"id": "' . $row[0] . '",';
			$resJSON .= '"login": "' . $row[1]. '",';
			$resJSON .= '"surname": "' . $row[2] . '",';
			$resJSON .= '"name": "' . $row[3] . '",';
			$resJSON .= '"patronymic": "' . $row[4] . '",';
			$resJSON .= '"birthDate": "' . $row[5] . '",';
			$resJSON .= '"registerDate": "' . $row[6] . '",';
			$resJSON .= '"role": "' . $row[7] . '"';
            $resJSON .= '}';
        }
		$resJSON .= ']';
        echo $resJSON;
	} elseif ($_SERVER['REQUEST_METHOD'] == 'POST'){
		if (!isset($_SESSION['user']))
            throw new Exception("Вы не авторизированы!");
		if ($_POST['arg'] != null)
			$res = pg_query_params($conn, 'SELECT * FROM update_acc($1)', array($_POST['arg']));
		else if($_POST['oldPass'] != null && $_POST['newPass'] != null)
			$res = pg_query_params($conn, 'SELECT * FROM change_password($1, $2, $3)', array($_SESSION['user']->getLogin(), $_POST['oldPass'], $_POST['newPass']));
		else throw new Exception("Аргументы не заданы!");
		if(!$res)
			displayError(pg_last_error($conn));
		else
			echo '{ "result": true }';
    } elseif ($_SERVER['REQUEST_METHOD'] == 'DELETE') {
        if (!isset($_SESSION['user']))
            throw new Exception("Вы не авторизированы!");
		$res = pg_query_params($conn, 'SELECT * FROM del_acc($1)', array($_SESSION['user']->getLogin()));
		if(!$res)
			displayError(pg_last_error($conn));
		else {
			unset($_SESSION['user']);
			echo '{ "result": true }';
		}
    }
} catch (Exception $e) {
    echo '{ "error": "', $e->getMessage(), '" }';
} finally {
    if ($conn)
        pg_close($conn);
}
?>