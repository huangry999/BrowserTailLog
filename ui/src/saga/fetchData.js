export const fetchHosts = async () => {
  const respond = await fetch('http://127.0.0.1:8080/log/host');
  return respond.json();
}