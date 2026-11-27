/**
 * MODELO - Resultado del login
 * El backend autentica contra la cuenta unificada y devuelve un JWT firmado
 * junto con el rol, el id del perfil y el nombre. El token se guarda en
 * localStorage; los demás datos solo viven en memoria.
 */
export interface ResultadoLogin {
  token: string;
  tipo: string;
  rol: string;
  id: number;
  nombre: string;
}
