<?php
function getConnection()
{
    $db_cred_file = fopen('db_credentials', 'rt');
    if (!$db_cred_file)
        throw new Exception('Не найден файл с данными соединения!');
    if (feof($db_cred_file))
        throw new Exception('Файл с данными соединения пуст!');
    $host = fgets($db_cred_file);
    $port = fgets($db_cred_file);
    $db_name = fgets($db_cred_file);
    $user = fgets($db_cred_file);
    $pass = fgets($db_cred_file);
    if (!$port || !$db_name || !$host || !$user || !$pass)
        throw new Exception('Ошибка чтения файла с данными соединения!');
    return pg_connect("host=" . $host . " port=" . $port . " dbname=" . $db_name . " user=" . $user
        . " password=" . $pass);// . " sslmode=require");
}

function displayError($message)
{
    if (strpos($message, 'CONTEXT') != false)
        throw new Exception(substr($message, 0, strpos($message, 'CONTEXT') - 1));
    else
        throw new Exception($message);
}

class User 
{
	private $login, $access = false, $admin = false;
	
	public function getLogin() {
		return $this->login;
	}
	
	public function setLogin($login) {
		$this->login = $login;
	}
	
	public function hasAccess() {
		return $this->access;
	}
	
	public function setAccess($access) {
		$this->access = $access;
	}
	
	public function isAdmin() {
		return $this->admin;
	}
	
	public function setAdmin($admin) {
		$this->admin = $admin;
	}
}	
?>
