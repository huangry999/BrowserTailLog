const config = {
  ip: window.location.hostname,
  port: window.location.port,
  wsPort: 10901,
}
if (localStorage.getItem('testPort')) {
  config.port = parseInt(localStorage.getItem('testPort'), 10);
}
export default config;