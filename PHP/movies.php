<?php
error_reporting(0);
require_once "utils.php";
session_start();

$conn = null;
try {
	$conn = getConnection();
    if ($_SERVER['REQUEST_METHOD'] == 'GET') {
        $res = pg_query_params($conn, "SELECT * FROM view_movies($1, $2, $3)",
                array(($_GET['search'] ? rawurldecode($_GET['search']) : null),
                    $_GET['page'], $_GET['pageSize']));
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
			//movie_id int4, title varchar, release_year int4, duration int4, description text, cover_url varchar, trailer_url varchar, rating_category varchar
			$resJSON .= '"id": "' . $row[0] . '",';
			$resJSON .= '"title": "' . $row[1] . '",';
			$resJSON .= '"releaseYear": "' . $row[2] . '",';
			$resJSON .= '"duration": "' . $row[3] . '",';
			$resJSON .= '"description": "' . $row[4] . '",';
			$resJSON .= '"coverUrl": "' . $row[5] . '",';
			$resJSON .= '"trailerUrl": "' . $row[6] . '",';
			$resJSON .= '"ratingCategory": "' . $row[7] . '",';
			$resJSON .= '"editable": ' . ($editable == true ? 'true' : 'false');
			$resJSON .= '}';
        }
		$resJSON .= ']';
        echo $resJSON;
    } elseif ($_SERVER['REQUEST_METHOD'] == 'POST') {
        if (!isset($_SESSION['user']))
            throw new Exception("Вы не авторизированы!");
		if ($_SESSION['user']->hasAccess() != true)
			throw new Exception('Нет доступа!');
		if ($_POST['arg'] != null) {
			if(json_decode($_POST['arg'])->id == 0)
				$res = pg_query_params($conn, 'SELECT * FROM new_movie($1)', array(rawurldecode($_POST['arg'])));
			else
				$res = pg_query_params($conn, 'SELECT * FROM update_movie($1)', array(rawurldecode($_POST['arg'])));
			if(!$res)
				displayError(pg_last_error($conn));
			else {
				if(json_decode($_POST['arg'])->id == 0)
					echo '{ "result": ', pg_fetch_result($res, 0, 0), '}';
				else
					echo '{ "result": true }';
			}
		} else throw new Exception("Аргументы не заданы!");
    } elseif ($_SERVER['REQUEST_METHOD'] == 'DELETE') {
        if (!isset($_SESSION['user']))
            throw new Exception("Вы не авторизированы!");
		if ($_SESSION['user']->hasAccess() != true)
			throw new Exception('Нет доступа!');
		if ($_GET['id'] != null) {
			$res = pg_query_params($conn, 'SELECT * FROM del_movie($1)', array($_GET['id']));
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