export interface AvaRemotePluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  initializePlugin(): Promise<void>;
  sendConfig(options: {config: Object}): Promise<void>;
}
