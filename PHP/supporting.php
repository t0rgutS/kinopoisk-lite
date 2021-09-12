<?php
error_reporting(0);
require_once "utils.php";
session_start();

$conn = null;
try {
	$conn = getConnection();
	$res = null;
	if($_GET['table'] == 'crew_roles')
		$res = pg_query($conn, 'SELECT * FROM crew_roles');
	elseif($_GET['table'] == 'halls')
		$res = pg_query($conn, 'SELECT * FROM halls');
	elseif($_GET['table'] == 'age_ratings')
		$res = pg_query($conn, 'SELECT * FROM age_ratings');
	elseif($_GET['table'] == 'visitor_roles') {
		if(isset($_SESSION['access']))
			$res = pg_query($conn, 'SELECT * FROM visitor_roles');
		else
			throw new Exception('Нет доступа!');
	} else
		throw new Exception('Справочник не выбран!');
	if(!$res)
		displayError(pg_last_error($conn));
	$resJSON = '[';
    while ($row = pg_fetch_row($res)) {
		if($resJSON != '[')
			$resJSON .= ',';
		$resJSON .= '{';
		$resJSON .= '"id": "' . $row[0] . '",';
		$resJSON .= '"name": "' . $row[1] . '"';
		$resJSON .= '}';
    }
	$resJSON .= ']';
    echo $resJSON;
} catch (Exception $e) {
    echo '{ "error": "Ошибка: ', $e->getMessage(), '" }';
} finally {
    if ($conn)
        pg_close($conn);
}
?>