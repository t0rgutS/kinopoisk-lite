<?php
error_reporting(0);
require_once "utils.php";
session_start();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    try {
		$conn = getConnection();
        $res = pg_query_params($conn, 'SELECT * FROM verify_user($1, $2)', array($_POST['login'], $_POST['password']));
        if (!$res)
            displayError(pg_last_error($conn));
        else {
            $success = pg_fetch_result($res, 0, 0);
            if ($success == true) {
                $user = new User();
				$user->setLogin($_POST['login']);
				pg_free_result($res);
				$res = pg_query_params($conn, 'SELECT * FROM has_rights($1)', array($_POST['login']));
				if(!$res)
					displayError(pg_last_error($conn));
				else {
					$user->setAccess(pg_fetch_result($res, 0, 0) == 't' ? true : false);
					pg_free_result($res);
					$res = pg_query_params($conn, 'SELECT * FROM is_admin($1)', array($_POST['login']));
					if(!$res)
						displayError(pg_last_error($conn));
					else
						$user->setAdmin(pg_fetch_result($res, 0, 0) == 't' ? true : false);
				}
				$_SESSION['user'] = $user;
			}
            echo '{ "result": ', ($success ? 'true' : 'false'),  ' }';
        }
    } catch (Exception $e) {
        echo '{ "error": "Ошибка: ', $e->getMessage(), '" }';
    }
}
?>