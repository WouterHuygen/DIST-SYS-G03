export interface Node {
  id: number;
  name: string;
  ip: string;
  replicatedFiles: string[];
  ownFiles: string[];
}
