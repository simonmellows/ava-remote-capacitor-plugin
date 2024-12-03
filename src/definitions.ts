export interface AvaRemotePluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
